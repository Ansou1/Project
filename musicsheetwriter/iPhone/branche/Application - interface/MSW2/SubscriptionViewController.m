//
//  SubscriptionViewController.m
//  MSW2
//
//  Created by simon on 11/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "SubscriptionViewController.h"
#import "ProfilViewController.h"
#import "GuestMode.h"

@interface SubscriptionViewController ()

@end

@implementation SubscriptionViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.tableView.allowsMultipleSelectionDuringEditing = NO;
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuestId:self] == true) {
        return;
    }
    [self refreshView];
}

-(void) refreshView{
    NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscriptions", Id_global];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];
    if (code_global != 200){
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

// this two methods are here to swipe a cell in tableview
- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
    // Return YES if you want the specified item to be editable.
    return YES;
}

// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
    if (editingStyle == UITableViewCellEditingStyleDelete) {
        //add code here for when you hit delete
        NSString * post =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/subscriptions/%@",Id_global, _idRow[indexPath.row]];
        ApiMethod *api = [[ApiMethod alloc]init];
        NSDictionary *dict1 = [api ApiMethodDeleteAfnetworkingAt:post];
        if (code_global != 204){
            [api popup:dict1 id:self];
            return;
        }
        [self refreshView];
        [tableView reloadData];
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
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
