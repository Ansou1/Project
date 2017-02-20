//
//  SuscriberTableViewController.m
//  MSW
//
//  Created by simon on 15/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "SuscriberTableViewController.h"
#import "ApiMethod.h"
#import "ProfilViewController.h"
#import "GuestMode.h"

@interface SuscriberTableViewController ()

@end

@implementation SuscriberTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuestId:self] == true) {
        return;
    }
    [self refreshView];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(void) refreshView{
    NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscribers", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];
    if (code_global != 200)
    {
        [api popup:dict1 id:self];
        return;
    }
    _pseudo = [[NSMutableArray alloc] initWithCapacity:0];
    _idRow = [[NSMutableArray alloc] initWithCapacity:0];
    for(NSDictionary *item in dict1) {
        [_pseudo addObject:[item valueForKey:@"username"]];
        [_idRow addObject:[item valueForKey:@"id"]];
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return _pseudo.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *cellId = @"SubscriptionCell";
    UITableViewCell *cell2 = [tableView dequeueReusableCellWithIdentifier:cellId forIndexPath:indexPath];
    cell2.textLabel.text = self.pseudo[indexPath.row];
    return cell2;
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    if ([[segue identifier] isEqualToString:@"ShowProfil"]){
        ProfilViewController *profilController = [segue destinationViewController];
        NSIndexPath *myIndexPath = nil;
        myIndexPath = [self.tableView indexPathForSelectedRow];
        profilController.ProfilModal = @[_pseudo[myIndexPath.row], _idRow[myIndexPath.row], _idRow[myIndexPath.row]];
    }
}

@end
