//
//  ProfilViewController.m
//  MSW2
//
//  Created by simon on 15/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "ProfilViewController.h"
#import "SWRevealViewController.h"
#import "AbonneesFromProfilTableViewController.h"
#import "GuestMode.h"
#import "ProfilTableViewController.h"

@interface ProfilViewController ()

@end

@implementation ProfilViewController

-(void)viewWillAppear:(BOOL)animated{
    alert_load = [UIAlertController alertControllerWithTitle:nil message:@"Veuillez patienter\n\n" preferredStyle:UIAlertControllerStyleAlert];
    UIActivityIndicatorView *spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    spinner.center = CGPointMake(130.5, 65.5);
    spinner.color = [UIColor blackColor];
    [spinner startAnimating];
    [alert_load.view addSubview:spinner];
    [self presentViewController:alert_load animated:NO completion:nil];
}

-(void)viewDidAppear:(BOOL)animated{
    [alert_load dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    _idProfil = _ProfilModal[1];
    idProfil_global = _idProfil;
    
    ApiMethod *api = [[ApiMethod alloc]init];
    NSString *url1 = [NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@", _idProfil];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:url1];
    
    if (code_global != 200)
    {
        [api popup:dict1 id:self];
        return;
    }
    //-------------------------------------------------------------------------------------------------------------------------------------------------------------
    // number of subscriptions
    NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscriptions", idProfil_global];
    NSDictionary *dict3 = [api ApiMethodGetAfnetworkingAt:post];
    
    if (code_global != 200)
    {
        [api popup:dict3 id:self];
        return;
    }
    _pseudo2 = [[NSMutableArray alloc] initWithCapacity:0];
    for(NSDictionary *item in dict3) {
        [_pseudo2 addObject:[item valueForKey:@"username"]];
    }
    _abonnements.text = [NSString stringWithFormat:@"%lu", (unsigned long)_pseudo2.count];
    //----------------------------------------------------------------------------------------------------------------------------------------------------------
    // number of subscribers
    post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscribers", idProfil_global];
    NSDictionary *dict4 = [api ApiMethodGetAfnetworkingAt:post];
    
    if (code_global != 200)
    {
        [api popup:dict4 id:self];
        return;
    }
    _pseudo3 = [[NSMutableArray alloc] initWithCapacity:0];
    for(NSDictionary *item in dict4) {
        [_pseudo3 addObject:[item valueForKey:@"username"]];
    }
    _abonnes.text = [NSString stringWithFormat:@"%lu", (unsigned long)_pseudo3.count];
    //----------------------------------------------------------------------------------------------------------------------------------------------------------
    [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
    
    self.profileImageView.layer.cornerRadius = self.profileImageView.frame.size.width / 2;
    self.profileImageView.clipsToBounds = YES;
    self.profileImageView.layer.borderWidth = 3.0f;
    self.profileImageView.layer.borderColor = [UIColor blackColor].CGColor;
    
    //upload photo
    NSDictionary *dict2;
    dict2 = [dict1 valueForKeyPath:@"personal_data"];
    if ([dict2 objectForKey:(@"photo")] != [NSNull null]){
        urlPhoto = [dict2 objectForKey:@"photo"];
    }
    if (code_global != 200){
        [api popup:dict1 id:self];
        return;
    }
    if (urlPhoto != NULL){
        _profileImageView.image = [UIImage imageWithData: [NSData dataWithContentsOfURL:[NSURL URLWithString:urlPhoto]]];
    }
    UITapGestureRecognizer* gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(actionTapAbonnes:)];
    [self.abonnes setUserInteractionEnabled:YES];
    [self.abonnes addGestureRecognizer:gesture];
    
    UITapGestureRecognizer* gesture2 = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(actionTapAbonnements:)];
    [self.abonnements setUserInteractionEnabled:YES];
    [self.abonnements addGestureRecognizer:gesture2];
    
    [self refreshStateButton];
}

-(BOOL) checkUserInTheList{
    int i = 0;
    
    NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscriptions", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];
    if (code_global != 200)
    {
        [api popup:dict1 id:self];
        return FALSE;
    }
    
    _pseudoCounter = [[NSMutableArray alloc] initWithCapacity:0];
    _idRowCounter = [[NSMutableArray alloc] initWithCapacity:0];
    
    for(NSDictionary *item in dict1) {
        [_idRowCounter addObject:[item valueForKey:@"id"]];
    }
    while (i < _idRowCounter.count)
    {
        if ([_idRowCounter[i] isEqualToString:_ProfilModal[1]])
        {
            return TRUE;
        }
        i = i + 1;
    }
    return FALSE;
}

-(void) refreshStateButton{
    
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuestId:self] != true) {
        if (([_star imageForState:UIControlStateNormal] != [UIImage imageNamed:@"star-2.png"]) && ([self checkUserInTheList]))
        {
            [_star setImage:[UIImage imageNamed:@"star-2.png"] forState:UIControlStateNormal];
        }
        else{
            [_star setImage:[UIImage imageNamed:@"star.png"] forState:UIControlStateNormal];
        }
    }
    else
    {
        [_star setImage:[UIImage imageNamed:@"star.png"] forState:UIControlStateNormal];
    }
}

-(void) actionTapAbonnes:(UIGestureRecognizer*)gesture{
    [self performSegueWithIdentifier:@"showAbonnés" sender:self];
}

-(void) actionTapAbonnements:(UIGestureRecognizer*)gesture{
    [self performSegueWithIdentifier:@"showAbonnements" sender:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    
    if ([[segue identifier] isEqualToString:@"ShowData"]){
        ProfilTableViewController *profil = [segue destinationViewController];
        profil.IdProfil = _ProfilModal[1];
    }
}

- (IBAction)star:(id)sender {
    
    if (![self checkUserInTheList]){
        
        ApiMethod *api = [[ApiMethod alloc]init];
        NSString * post1 =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscriptions", Id_global];
        NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
        [parametersDictionary setValue:_idProfil forKey:@"id"];
        NSDictionary *dict1 = [api ApiMethodPostAfnetworkingAt:post1 Data:parametersDictionary];
        if (code_global != 200){
            [api popup:dict1 id:self];
            return;
        }
        [self refreshStateButton];
        [self viewDidLoad];
    }
    else{
        NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscriptions/%@",Id_global , _idProfil];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSDictionary *dict1 = [api ApiMethodDeleteAfnetworkingAt:post];
        if (code_global != 204){
            [api popup:dict1 id:self];
            return;
        }
        [self refreshStateButton];
        [self viewDidLoad];
    }
}

@end

